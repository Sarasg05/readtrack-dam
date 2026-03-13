from rest_framework import serializers
from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession


class AnnualGoalSerializer(serializers.ModelSerializer):
    class Meta:
        model = AnnualGoal
        fields = ['id', 'user', 'year', 'target_books']


class AuthorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Author
        fields = ['id', 'name']


class GenreSerializer(serializers.ModelSerializer):
    class Meta:
        model = Genre
        fields = ['id', 'name']


class BookSerializer(serializers.ModelSerializer):
    class Meta:
        model = Book
        fields = ['id', 'title', 'author', 'total_pages', 'synopsis', 'genres']


class ReadingSerializer(serializers.ModelSerializer):
    class Meta:
        model = Reading
        fields = ['id', 'user', 'book', 'start_date', 'end_date', 'status']


class ReadingSessionSerializer(serializers.ModelSerializer):
    class Meta:
        model = ReadingSession
        fields = ['id', 'reading', 'date', 'pages_read', 'minutes_read']